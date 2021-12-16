package io.dongtai.rasp.engine.verify;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * @author owefsad
 */
public class LoggerConfigAdviceAdapter extends AdviceAdapter {

    private String targetClass = " org.apache.logging.log4j.LogManager".substring(1);
    private ClassLoader classLoader;
    private String dnsLogAddr;

    public LoggerConfigAdviceAdapter(int api, MethodVisitor methodVisitor, int access, String name,
            String descriptor, ClassLoader classLoader, String dnsLogAddr) {
        super(api, methodVisitor, access, name, descriptor);
        this.classLoader = classLoader;
        this.dnsLogAddr = dnsLogAddr;
    }

    @Override
    protected void onMethodEnter() {
        try {
            verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean verify()
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class logManagerOfClass = classLoader.loadClass(targetClass);
        Object logger = logManagerOfClass.getMethod("getLogger", Object.class).invoke(null, "Log4jVerify");
        Method errorOfMethod = logger.getClass().getMethod("error", String.class, Object.class);
        errorOfMethod.invoke(
                logger,
                "vul is :{}", "${jndi:ldap:// " + dnsLogAddr + "}"
        );
        return true;
    }
}
