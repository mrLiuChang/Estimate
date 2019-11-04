package opt;

import java.lang.reflect.Method;

public class RetryUtil {
    private static ThreadLocal<Integer> retryTimesInThread = new ThreadLocal<>();

    public static RetryUtil setRetryTimes(Integer retryTimes) {
        if (retryTimesInThread.get() == null)
            retryTimesInThread.set(retryTimes);
        return new RetryUtil();
    }


    public Object retry(Object... args) {
        try {
            Integer retryTimes = retryTimesInThread.get();
            if (retryTimes <= 0) {
                retryTimesInThread.remove();
                return null;
            }
            retryTimesInThread.set(--retryTimes);
            String upperClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            String upperMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();

            Class clazz = Class.forName(upperClassName);

            Object targetObject = args[0];
            Object[] targetArgs = new Object[args.length - 1];
            System.arraycopy(args, 1, targetArgs, 0, targetArgs.length);

            //Object targetObject = clazz.newInstance();
            Method targetMethod = null;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(upperMethodName)) {
                    targetMethod = method;
                    break;
                }
            }
            if (targetMethod == null)
                return null;
            targetMethod.setAccessible(true);
            return targetMethod.invoke(targetObject, targetArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}