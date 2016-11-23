import com.alibaba.dcm.DnsCacheManipulator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by migle on 2016/8/2.
 */
public class DNSTest {
    public static void main(String[] args) throws NoSuchMethodException, UnknownHostException, IllegalAccessException, InvocationTargetException, InstantiationException {
        DnsCacheManipulator.setDnsCache("vm-centos-00", "10.113.149.145");
//        DnsCacheManipulator.setDnsCache("nit-v5-qcd02", "10.113.149.146");
//        DnsCacheManipulator.setDnsCache("nit-v5-qcd03", "10.113.149.147");
//        System.out.println(InetAddress.getByName("nit-v5-qcd04").getHostAddress());
//        InetAddress.getLocalHost().getHostAddress();
//        System.out.println("==============");
//        String className = "java.net.InetAddress$CacheEntry";
//        Class<?> clazz = Class.forName(className);
//        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
//        constructor.setAccessible(true);
        //return constructor.newInstance(toInetAddressArray(host, ips), expiration);


        //Method cm = InetAddress.class.getMethod("cacheAddresses",String.class,InetAddress[].class,Boolean.class);
        //System.out.println(System.getProperty("java.security.manager"));
        System.out.println("================");

        Method cm = InetAddress.class.getMethod("cacheAddresses", String.class, InetAddress[].class, Boolean.class);
        cm.setAccessible(true);

        Constructor<?> inetadress = InetAddress.class.getDeclaredConstructors()[0];
        inetadress.setAccessible(true);
        InetAddress iao = (InetAddress) inetadress.newInstance();

        final Object invoke = cm.invoke(iao, "vm-centos-00", new InetAddress[]{InetAddress.getLocalHost()}, true);


        System.out.println(InetAddress.getByName("vm-centos-00"));
//        try {
//            Field acf = InetAddress.class.getDeclaredField("addressCache");
//            acf.setAccessible(true);
//            Object addressCache = acf.get(null);
//            Class cacheKlass = addressCache.getClass();
//            Field cf = cacheKlass.getDeclaredField("cache");
//            cf.setAccessible(true);
//            Map<String, Object> cache = (Map<String, Object>) cf.get(addressCache);
//            for(Map.Entry<String,Object> hi :cache.entrySet()){
//                Object cacheEntry = hi.getValue();
//                Class cacheEntryKlass = cacheEntry.getClass();
//                Field expf = cacheEntryKlass.getDeclaredField("expiration");
//                expf.setAccessible(true);
//                long expires = (Long) expf.get(cacheEntry);
//
//                Field af = cacheEntryKlass.getDeclaredField("address");
//                af.setAccessible(true);
//                InetAddress[] addresses = (InetAddress[]) af.get(cacheEntry);
//                List<String> ads = new ArrayList<String>(addresses.length);
//                for (InetAddress address : addresses) {
//                    ads.add(address.getHostAddress());
//                }
//
//                System.out.println(hi.getKey() + " "+new Date(expires) +" " +ads);
//            }
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }
}
