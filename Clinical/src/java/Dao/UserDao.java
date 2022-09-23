/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dao;


import Domain.User;
import Util.HU;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Rugwiro
 */
public class UserDao extends GenericDao<User>{
    public List<User> loginencrypt(String u, String pass) throws Exception {

        Session s = HU.getSessionFactory().openSession();
        List<User> list = new ArrayList<>();

        List<User> users = new UserDao().findAll(User.class);
        String z = "";
        for (User us : users) {
            if (us.getUsername().matches(u)) {
                if (us.getPassword().matches(pass)) {
                    list.add(us);
                }
            }

        }

        s.close();
        return list;

    }
    
        public List<User> login(String psw,String un){
      Session  s=HU.getSessionFactory().openSession();
      Query q=s.createQuery("from User s where s.password= :v and s.username= :l");
      q.setString("v", psw);
       q.setString("l", un);
      List<User> u=q.list();
      return u;
    } 
    
}
