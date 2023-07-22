package com.driver.Service;

import com.driver.Group;
import com.driver.Message;
import com.driver.User;
import com.driver.Repository.WhatsappRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {

    WhatsappRepo whatsappRepo= new WhatsappRepo();
    public String createUser(String name, String mobile) throws Exception {
        return whatsappRepo.createUser(name,mobile);
    }

    public Group createGroup(List<User> users) {
        return whatsappRepo.createGroup(users);
    }

    public int createMessage(String content) {
        return whatsappRepo.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        return whatsappRepo.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        return whatsappRepo.changeAdmin(approver,user,group);
    }

    public int removeUser(User user) throws Exception {
        return whatsappRepo.removeUser(user);
    }

    public String findMessage(Date start, Date end, int k) {
        return whatsappRepo.finMessage(start,end,k);
    }
}
