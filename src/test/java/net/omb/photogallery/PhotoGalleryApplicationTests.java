//package net.omb.photogallery;
//
//import net.omb.photogallery.model.Role;
//import net.omb.photogallery.model.User;
//import net.omb.photogallery.repositories.UsersRepository;
//import org.apache.derby.drda.NetworkServerControl;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.net.InetAddress;
//
//import static org.junit.Assert.assertTrue;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class PhotoGalleryApplicationTests {
////    @Before
////    public void startDerbyNetworkServer() {
////        try {
////            NetworkServerControl server = new NetworkServerControl
////                    (InetAddress.getByName("localhost"), 1527);
////
////            server.start(null);
////        } catch (Exception e) {
////            throw new RuntimeException("Could not start derby network server" + e.getMessage());
////        }
////    }
//
//    @Autowired
//    private UsersRepository usersRepository;
//
//    @Test
//    public void persistanceTest() {
//        User user = new User("bodik@list.ru", "Aq1sw2de3", Role.ADMIN, true);
//        usersRepository.save(user);
//        User foundUser = usersRepository.findById(user.getId()).orElseGet(null);
//        assertTrue(foundUser.equals(user));
//    }
//
//}
