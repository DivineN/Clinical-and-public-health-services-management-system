/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import Dao.LocationDao;
import Dao.UserDao;
import Util.HU;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Rugwiro
 */
public class Main {

    public static void main(String[] args) {
        HU.getSessionFactory().openSession();

        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setAccess("Admin");
        UserDao userDao = new UserDao();
        userDao.save(user);

        List<String> Kigali = Arrays.asList("Gasabo", "Kicukiro", "Nyarugenge");
        List<String> North = Arrays.asList("Gicumbi", "Gakenke", "Burera", "Rulindo", "Musanze");
        List<String> West = Arrays.asList("Rubavu", "Nyamasheke", "Rusizi", "Rutsiro", "Nyabihu", "Ngororero", "Karongi");
        List<String> East = Arrays.asList("Nyagatare", "Gatsibo", "Rwamagana", "Ngoma", "Kayonza", "Kirehe", "Bugesera");
        List<String> South = Arrays.asList("Huye", "Gisagara", "Nyanza", "Ruhango", "Nyaruguru", "Nyamagabe", "Muhanga", "Kamonyi");

        Map<String, List<String>> map = new HashMap<>();
        map.put("KIGALI", Kigali);
        map.put("NORTH", North);
        map.put("WEST", West);
        map.put("EAST", East);
        map.put("SOUTH", South);

        for (Map.Entry<String, List<String>> mm : map.entrySet()) {
            Location p = new Location();
            p.setLocationName(mm.getKey());
            p.setLocationType("PROVINCE");
            new LocationDao().save(p);

            for (String instring : mm.getValue()) {
                Location loc = new Location();
                loc.setLocationName(instring);
                loc.setLocationType("DISTRICT");
                loc.setLocation(p);
                new LocationDao().save(loc);

            }
        }

    }

}
