package com.driver;

import com.driver.Group;
import com.driver.Message;
import com.driver.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepo {
    HashMap<String, String> userDb= new HashMap<>();
    HashMap<Group, List<User>>  groupUserDb= new HashMap<>();
    HashMap<Group, List<Message>> groupMessageMap= new HashMap<>();
    HashMap<Group,User> adminMap= new HashMap<>();
    HashMap<Message,User> senderMap= new HashMap<>();
    private int groupCount=1;
    private int messageId=1;
    public String createUser(String name, String mobile) throws Exception {
        if(userDb.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        userDb.put(mobile,name);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        // If group is successfully created, return group.

        //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
        //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.
        Group group= new Group();
        if(users.size()==2){
            String name= users.get(1).getName();
            group.setName(name);
            group.setNumberOfParticipants(2);

        }
        else{
            String groupName="Group "+ groupCount;
            group.setName(groupName);
            group.setNumberOfParticipants(userDb.size());
            groupCount++;
        }
        groupUserDb.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }

    public int createMessage(String content) {
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        Message message = new Message();
        message.setContent(content);
        message.setId(messageId);
        messageId++;
       return message.getId();

    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

        if(!groupUserDb.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        senderMap.put(message,sender);
        List<User> usersList = new ArrayList<>(groupUserDb.get(group));
        for(User x: usersList) {
            if (sender.getMobile().equals(x.getMobile())) {
                if (groupMessageMap.containsKey(group)) {
                    groupMessageMap.get(group).add(message);
                    return groupMessageMap.get(group).size();
                } else {
                    List<Message> messages = new ArrayList();
                    messages.add(message);
                    groupMessageMap.put(group, messages);
                }
            }
        }
        throw new Exception("You are not allowed to send message");

    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not
        // the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(!groupUserDb.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).getMobile().equals(approver.getMobile())){
            throw new Exception("Approver does not have rights");
        }
        List<User> usersList= new ArrayList<>(groupUserDb.get(group));
        for(User i: usersList){
            if(i.getMobile().equals(user.getMobile())){
                adminMap.put(group,user);
                return "SUCCESS";
            }
        }
        throw new Exception("User is not a participant");
    }

    public int removeUser(User user) throws Exception {

        int sum=0;

        for (Group x:groupUserDb.keySet() ) {
            List <User> users= groupUserDb.get(x);
            for(User i: users){
                //If user is found in a group
                if(i.getMobile().equals(user.getMobile())) {
                    //If user is found in a group and it is the admin
                    if (adminMap.get(x).equals(i)) {
                        throw new Exception("Cannot remove the admin");
                    } else {
                        List<Message> messages = new ArrayList<>(groupMessageMap.get(x));
                        for (Message m : messages) {
                            // removing from sender map
                            if (senderMap.get(m).getMobile().equals(user.getMobile())) {
                                // removing from message list
                                messages.remove(m);
                                senderMap.remove(m);
                            }
                            groupMessageMap.get(x).remove(m);
                        }
                    }
                    groupUserDb.remove(i);
                    x.setNumberOfParticipants(users.size());

                }
                else {
                    throw new Exception("User not found");
                }
                //    the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
                sum+=x.getNumberOfParticipants()+ groupMessageMap.get(x).size()+senderMap.size();
            }

        }
        return sum;
    }

    public String finMessage(Date start, Date end, int k) {
        return "";
    }
}

