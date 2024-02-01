package main.service.impl;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.City;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserMin;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import main.service.UserLoaderNet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserLoaderNetImpl implements UserLoaderNet {

    private static final Logger logger = LogManager.getLogger(UserLoaderNetImpl.class);

    @Value("${access.token}")
    private String ACCESS_TOKEN;

    @Value("${api.id}")
    private int API_ID;

    private final DataSource dataSource;

    @Autowired
    public UserLoaderNetImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean getUserData() {
        try {
            List<Integer> listId = getUserIds();
            if (CollectionUtils.isEmpty(listId)) {
                logger.warn("Не найдено ни одного userId для поиска");
                return false;
            }
            loadUsers(listId);
            return true;
        } catch (Exception e) {
            StackTraceElement ste = e.getStackTrace()[0];
            String message = MessageFormat.format("Ошибка {0} в классе {1} методе {2} строке {3}.", e.getMessage(), ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            if (e.getCause() != null) message = message.concat(" Причина: ").concat(e.getCause().getMessage());
            logger.error(message);
            return false;
        }
    }

    private List<Integer> getUserIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "select user_id from vk_user";
        try (Connection connection = dataSource.getConnection(); Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
            rs.close();
        }
        return ids;
    }

    private boolean loadUsers(List<Integer> idList) throws SQLException, ClientException, ApiException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        String ids = idList.stream().map(Objects::toString).collect(Collectors.joining(","));
        List<GetResponse> responseList = vk.users()
                .get(new UserActor(API_ID, ACCESS_TOKEN))
                .userIds(ids)
                .fields(Fields.BDATE, Fields.CITY, Fields.CONTACTS)
                .execute();
        if (responseList.isEmpty()) return false;
        checkData(idList, responseList);
        saveUsers(responseList);
        return true;
    }

    private void checkData(List<Integer> inputIds, List<GetResponse> responseList) {
        List<Integer> resultIds = responseList.stream().map(UserMin::getId).toList();
        for (Integer id : inputIds) {
            if (!resultIds.contains(id)) {
                logger.warn(MessageFormat.format("Пользователь с id: {0} не найден", id));
            }
        }
    }


    private void saveUsers(List<GetResponse> responseList) throws SQLException {
        String query = "update vk_user set user_f_name = ?, user_l_name = ?, user_b_date = ?, user_city = ?, user_contacts = ? where user_id = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement pst = connection.prepareStatement(query)) {
            for (GetResponse response : responseList) {
                pst.setString(1, response.getFirstName());
                pst.setString(2, response.getLastName());
                pst.setString(3, response.getBdate());
                pst.setString(4, getCity(response.getCity()));
                pst.setString(5, getContacts(response));
                pst.setInt(6, response.getId());
                pst.executeUpdate();
            }
        }
    }


    private String getCity(City city) {
        return city == null ? null : city.getTitle();
    }

    private String getContacts(GetResponse response) {
        String contact = StringUtils.isNotEmpty(response.getMobilePhone()) ? response.getMobilePhone() : null;
        if (contact == null) {
            contact = StringUtils.isNotEmpty(response.getHomePhone()) ? response.getMobilePhone() : null;
        } else {
            contact = StringUtils.isNotEmpty(response.getHomePhone()) ? contact.concat(",").concat(response.getHomePhone()) : contact;
        }
        return contact;
    }

}
