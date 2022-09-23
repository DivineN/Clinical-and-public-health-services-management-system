/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.UserDao;
import Domain.User;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class UserModel {

    private User user = new User();

    private User userDetails = new User();

    private UserDao userDao = new UserDao();

    private List<User> users;

    private String username = new String();

    private String password = new String();

    private String userdetails = new String();

    private String sid = new String();

    private String sectid = new String();

    @PostConstruct
    public void init() {
//        users = userDao.FindAll(User.class);
    }

    public String login() throws IOException, Exception {
        String msg = "";
        findUser();
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        Optional<User> op = Optional.ofNullable(user);
        if (user != null) {

            if (user.getAccess().matches("Admin")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("session", user);
                ec.redirect(ec.getRequestContextPath() + "/faces/Dashboard/Admin/index.xhtml");
                msg = "faces/Dashboard/Admin/index.xhtml?faces-redirect=true";
            } else if (user.getAccess().matches("Doctor")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("session", user);
                ec.redirect(ec.getRequestContextPath() + "/faces/Dashboard/Doctor/index.xhtml");
                msg = "faces/Dashboard/Doctor/index.xhtml?faces-redirect=true";
            } else if (user.getAccess().matches("Hospital")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("session", user);
                ec.redirect(ec.getRequestContextPath() + "/faces/Dashboard/Hospital/index.xhtml");
                msg = "faces/Dashboard/Hospital/index.xhtml?faces-redirect=true";
            } else if (user.getAccess().matches("Patient")) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("session", user);
                ec.redirect(ec.getRequestContextPath() + "/faces/Dashboard/Patients/index.xhtml");
                msg = "faces/Dashboard/Patients/index.xhtml?faces-redirect=true";
            } 
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,"Invalid Credentials!",null));
  
        }
        return msg;

    }

    public void findUser() throws Exception {
        List<User> usersLogin = new UserDao().loginencrypt(username, password);

        if (!usersLogin.isEmpty()) {
            for (User u : usersLogin) {
                user = u;
            }
        } else {
            user = null;
        }
    }

    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        user = null;
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/faces/Account/login.xhtml");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User userDetails) {
        this.userDetails = userDetails;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserdetails() {
        return userdetails;
    }

    public void setUserdetails(String userdetails) {
        this.userdetails = userdetails;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSectid() {
        return sectid;
    }

    public void setSectid(String sectid) {
        this.sectid = sectid;
    }

}
