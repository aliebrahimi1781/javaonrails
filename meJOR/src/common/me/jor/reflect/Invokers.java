package me.jor.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import me.jor.common.Task;
import me.jor.util.Cache;
import me.jor.util.concurrent.ExecutingOnce;

import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * 快速动态方法调用器
 * 
 * thank http://www.iteye.com/topic/1129340
 */
public class Invokers {
	/**
	 * 调用器池
	 */
	private static final Cache<Method, Invoker> INVOKER_CACHE = Cache.getCache(Invokers.class.getName()+".INVOKER_CACHE");
	/**
	 * 公共调用器池
	 */
	private static final Cache<Method, Invoker> PUBLIC_INVOKER_CACHE = Cache.getCache(Invokers.class.getName()+".PUBLIC_INVOKER_CACHE");
	
	private static ClassPool classPool;
	
	private static ClassPool getClassPool(){
		if(classPool==null){
			synchronized(ClassPool.class){
				if(classPool==null){
					classPool=ClassPool.getDefault();
					classPool.appendSystemPath();
				}
			}
		}
		return classPool;
	}
	public static void addClassPath(String path) throws NotFoundException{
		getClassPool().insertClassPath(path);
	}
	/**
	 * 调用器接口
	 */
	public static interface Invoker {
		/**
		 * 获取方法本身
		 * 
		 * @return
		 */
		Method method();

		/**
		 * 调用方法
		 * 
		 * @param host
		 *            执行对象
		 * @param args
		 *            执行参数
		 * @return
		 */
		Object invoke(Object host, Object... args);
	}

	/**
	 * 输出类型信息
	 * 
	 * @param argTypes
	 * @return
	 */
	private static String argumentTypesToString(Class<?>[] argTypes) {
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++) {
				if (i > 0) {
					buf.append(",");
				}
				Class<?> c = argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
			}
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * 快捷调用公共方法（性能较差）
	 * 
	 * @param host
	 *            宿主对象
	 * @param name
	 *            方法名
	 * @param args
	 *            方法参数
	 * @return 执行结果
	 * @throws NoSuchMethodException
	 *             如果没有相应的方法
	 */
	public static Object invokePublic(Object host, String name, Object... args)
			throws NoSuchMethodException {
		final Class<?> clazz = host instanceof Class ? (Class<?>) host : host
				.getClass();
		args = args == null ? new Object[] { null } : args;
		Class<?>[] paramTypes = new Class[args.length];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = args[i] == null ? null : args[i].getClass();
		}
		final Method method = MethodUtils.getMatchingAccessibleMethod(clazz,
				name, paramTypes);
		if (method == null) {
			throw new NoSuchMethodException(clazz.getName() + "." + name
					+ argumentTypesToString(paramTypes));
		}
		Invoker invoker = PUBLIC_INVOKER_CACHE.get(method);
		if (invoker == null) {
			invoker=newInvoker(method);
			PUBLIC_INVOKER_CACHE.putIfAbsent(method, invoker);
		}
		return invoker.invoke(host, args);
	}

	/**
	 * 根据传入的方法创建快速调用器。 比cglib的性能快4到40倍之间。
	 * 
	 * @param method
	 *            方法对象
	 * @return 调用器
	 */
	public static Invoker newInvoker(final Method method) {
		Invoker invoker = INVOKER_CACHE.get(method);
		if (invoker == null) {
			try {
				invoker=ExecutingOnce.executeAndWait(method.toGenericString(),new Task(){
					@Override
					public <T> T execute() throws Throwable {
						Invoker invoker=null;
						String proxyClassName = "proxy.invoker.method$"+System.identityHashCode(method);
						Class<?> proxyClass;
						try {
							proxyClass = Class.forName(proxyClassName);
						} catch (Throwable e) {
							ClassPool cp = getClassPool();
							CtClass cc = cp.makeClass(proxyClassName);
							cc.addField(CtField.make(
									"private java.lang.reflect.Method m;", cc));
							CtConstructor ctConstructor = new CtConstructor(
									new CtClass[] { cp.get(Method.class.getName()) },
									cc);
							ctConstructor.setBody("{this.m=(java.lang.reflect.Method)$1;}");
							cc.addConstructor(ctConstructor);
							cc.addInterface(cp.get(Invoker.class.getName()));
							cc.addMethod(CtMethod.make("public java.lang.reflect.Method method(){return m;}",cc));
							StringBuilder invokeCode = new StringBuilder("public Object invoke(Object host, Object[] args){");
							StringBuilder parameterCode = new StringBuilder();
							Class<?>[] parameterTypes=method.getParameterTypes();
							int parameterTypeLength=parameterTypes.length;
							for (int i = 0; i < parameterTypeLength; i++) {
								if (i > 0) {parameterCode.append(",");}
								Class<?> parameterType = parameterTypes[i];
								parameterCode.append(generateCast("args[" + i + "]",Object.class, parameterType));
							}

							StringBuilder executeCode = new StringBuilder();
							if (method.getParameterTypes().length > 0) {
								executeCode.append("if(args==null||args.length!=");
								executeCode.append(parameterTypeLength);
								executeCode.append(")throw new IllegalArgumentException(\"wrong number of arguments\");");
							}
							executeCode.append("((");
							executeCode.append(method.getDeclaringClass().getName());
							executeCode.append(")");
							executeCode.append(Modifier.isStatic(method.getModifiers()) ? "" : "host");
							executeCode.append(").");
							executeCode.append(method.getName());
							executeCode.append("(");
							executeCode.append(parameterCode);
							executeCode.append(")");

							if (!method.getReturnType().equals(Void.TYPE)) {
								invokeCode.append("return ");
								invokeCode.append(generateCast(executeCode.toString(),
										method.getReturnType(), Object.class));
								invokeCode.append(";");
							} else {
								invokeCode.append(executeCode);
								invokeCode.append(";return null;");
							}
							invokeCode.append("}");
							System.out.println(invokeCode);
							cc.addMethod(CtMethod.make(invokeCode.toString(), cc));
							proxyClass = cc.toClass();
						}
						invoker = (Invoker) proxyClass.getConstructor(Method.class)
								.newInstance(method);
						INVOKER_CACHE.putIfAbsent(method, invoker);
						return (T)invoker;
					}
					
				});
			} catch (Throwable e) {
				if(e instanceof RuntimeException){
					throw (RuntimeException)e;
				}else{
					throw new RuntimeException(e.getMessage(),e);
				}
			}
		}
		return invoker;
	}

	/**
	 * 快速动态调用宿主方法。 如果指定了方法名，则执行方法时只会调用指定了的方法。 <br/>
	 * 如果没有指定方法名，则调用宿主中对应接口类的同名方法。
	 * 
	 * @param superClass
	 *            接口类
	 * @param hostClass
	 *            宿主类
	 * @param methodName
	 *            宿主方法名（可选）
	 * @param hostMethodParameterTypes
	 *            宿主方法参数（可选）
	 * @param hostMethodReturnType
	 *            宿主方法返回类型（可选）
	 * @return 代理实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInvoker(Class<T> superClass, Class<?> hostClass,
			String methodName, Class<?>[] hostMethodParameterTypes,
			Class<?> hostMethodReturnType) {
		try {
			methodName = methodName == null ? null : methodName.trim();
			StringBuilder proxyClassNameBuilder = new StringBuilder();
			proxyClassNameBuilder.append("proxy.invoker$");
			proxyClassNameBuilder.append(superClass.hashCode() + 10000000000L);
			proxyClassNameBuilder.append("$");
			proxyClassNameBuilder.append(hostClass.hashCode() + 10000000000L);
			proxyClassNameBuilder.append("$");
			if (methodName != null && !methodName.equals("")) {
				proxyClassNameBuilder.append(methodName);
			}
			proxyClassNameBuilder.append("$");
			if (hostMethodParameterTypes != null
					&& hostMethodParameterTypes.length > 0) {
				proxyClassNameBuilder.append(10000000000L + Arrays
						.hashCode(hostMethodParameterTypes));
			}
			proxyClassNameBuilder.append("$");
			if (hostMethodReturnType != null) {
				proxyClassNameBuilder
						.append(10000000000L + hostMethodReturnType.hashCode());
			}
			String proxyClassName = proxyClassNameBuilder.toString();
			Class<?> proxyClass;
			try {
				proxyClass = Class.forName(proxyClassName);
			} catch (Exception ex) {
				ClassPool cp = new ClassPool(true);
				CtClass cc = cp.makeClass(proxyClassName);
				if (superClass.isInterface()) {
					cc.addInterface(cp.get(superClass.getName()));
				} else {
					cc.setSuperclass(cp.get(superClass.getName()));
				}
				Method[] methods = superClass.getMethods();
				for (Method method : methods) {
					int mod = method.getModifiers();
					if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
						continue;
					}
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length < 1
							|| (!hostClass.isAssignableFrom(parameterTypes[0]) && !parameterTypes[0]
									.isAssignableFrom(hostClass))) {
						throw new IllegalArgumentException(
								"The first argument is not a host instance");
					}
					if (hostMethodParameterTypes != null
							&& hostMethodParameterTypes.length != parameterTypes.length - 1) {
						throw new IllegalArgumentException(
								String.format(
										"The host method parameter types'number should be %d",
										parameterTypes.length - 1));
					}
					Class<?> returnType = method.getReturnType();
					StringBuilder methodCode = new StringBuilder();
					StringBuilder paramCode = new StringBuilder();
					methodCode.append("public ");
					methodCode.append(returnType.getCanonicalName());
					methodCode.append(" ");
					methodCode.append(method.getName());
					methodCode.append("(");
					for (int i = 0; i < parameterTypes.length; i++) {
						String canonicalName = parameterTypes[i]
								.getCanonicalName();
						if (i > 0) {
							methodCode.append(",");
							if (i > 1) {
								paramCode.append(",");
							}

							if (hostMethodParameterTypes != null) {
								String param = generateCast("p" + i,parameterTypes[i],hostMethodParameterTypes[i - 1]);
								paramCode.append(param);
							} else {
								String param = generateCast("p" + i,parameterTypes[i],parameterTypes[i - 1]);
								paramCode.append(param);
							}
						}
						methodCode.append(canonicalName);
						methodCode.append(" p");
						methodCode.append(i);
					}
					methodCode.append("){");
					StringBuilder executeCode = new StringBuilder();
					executeCode.append("((");
					executeCode.append(hostClass.getCanonicalName());
					executeCode.append(")p0).");
					if (methodName == null) {
						executeCode.append(method.getName());
					} else {
						executeCode.append(methodName);
					}
					executeCode.append("(");
					executeCode.append(paramCode);
					executeCode.append(")");
					if (!returnType.equals(Void.TYPE)) {
						methodCode.append("return ");
						hostMethodReturnType = hostMethodReturnType == null ? returnType
								: hostMethodReturnType;
						String returnCode = generateCast(
								executeCode.toString(), hostMethodReturnType,
								returnType);
						methodCode.append(returnCode);
					} else {
						methodCode.append(executeCode);
					}
					methodCode.append(";}");
					System.out.println(methodCode);
					cc.addMethod(CtMethod.make(methodCode.toString(), cc));
				}
				proxyClass = cc.toClass();
			}
			return (T) proxyClass.newInstance();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		}
	}

	private static String generateCast(String arg, Class<?> fromClass, Class<?> toClass) {
		StringBuilder cast = new StringBuilder();
		if (fromClass.isPrimitive() && !toClass.isPrimitive()) {
			Class<?> wraperClass = toClass;
			if (!isWraper(toClass)) {
				wraperClass = getWraper(fromClass);
			}
			cast.append("(");
			cast.append(toClass.getCanonicalName());
			cast.append(")");
			cast.append(wraperClass.getCanonicalName());
			cast.append(".valueOf((");
			cast.append(getPrimitive(wraperClass).getCanonicalName());
			cast.append(")");
			cast.append(arg);
			cast.append(")");
		} else if (!fromClass.isPrimitive() && toClass.isPrimitive()) {
			cast.append("(");
			cast.append(toClass.getCanonicalName());
			cast.append(")");
			Class<?> wraperClass = fromClass;
			if (!isWraper(fromClass)) {
				wraperClass = getWraper(toClass);
				cast.append("((");
				if (Number.class.isAssignableFrom(wraperClass)) {
					cast.append(Number.class.getCanonicalName());
				} else {
					cast.append(wraperClass.getCanonicalName());
				}
				cast.append(")");
				cast.append(arg);
				cast.append(")");
			} else {
				cast.append(arg);
			}
			cast.append(".");
			cast.append(getPrimitive(wraperClass).getCanonicalName());
			cast.append("Value()");
		} else {
			cast.append("(");
			cast.append(toClass.getCanonicalName());
			cast.append(")");
			cast.append(arg);
		}
		return cast.toString();
	}

	private static Class<?> getPrimitive(Class<?> wraperClass) {
		if (wraperClass.equals(Integer.class)) {
			return Integer.TYPE;
		}
		if (wraperClass.equals(Short.class)) {
			return Short.TYPE;
		}
		if (wraperClass.equals(Long.class)) {
			return Long.TYPE;
		}
		if (wraperClass.equals(Float.class)) {
			return Float.TYPE;
		}
		if (wraperClass.equals(Double.class)) {
			return Double.TYPE;
		}
		if (wraperClass.equals(Byte.class)) {
			return Byte.TYPE;
		}
		if (wraperClass.equals(Character.class)) {
			return Character.TYPE;
		}
		if (wraperClass.equals(Boolean.class)) {
			return Boolean.TYPE;
		}
		if (wraperClass.equals(Void.class)) {
			return Void.TYPE;
		}
		return wraperClass;
	}

	private static Class<?> getWraper(Class<?> toClass) {
		if (toClass.equals(Integer.TYPE)) {
			return Integer.class;
		}
		if (toClass.equals(Short.TYPE)) {
			return Short.class;
		}
		if (toClass.equals(Long.TYPE)) {
			return Long.class;
		}
		if (toClass.equals(Float.TYPE)) {
			return Float.class;
		}
		if (toClass.equals(Double.TYPE)) {
			return Double.class;
		}
		if (toClass.equals(Byte.TYPE)) {
			return Byte.class;
		}
		if (toClass.equals(Character.TYPE)) {
			return Character.class;
		}
		if (toClass.equals(Boolean.TYPE)) {
			return Boolean.class;
		}
		if (toClass.equals(Void.TYPE)) {
			return Void.class;
		}
		return toClass;
	}

	private static boolean isWraper(Class<?> toClass) {
		if (toClass.equals(Integer.class)) {
			return true;
		}
		if (toClass.equals(Short.class)) {
			return true;
		}
		if (toClass.equals(Long.class)) {
			return true;
		}
		if (toClass.equals(Float.class)) {
			return true;
		}
		if (toClass.equals(Double.class)) {
			return true;
		}
		if (toClass.equals(Byte.class)) {
			return true;
		}
		if (toClass.equals(Character.class)) {
			return true;
		}
		if (toClass.equals(Boolean.class)) {
			return true;
		}
		if (toClass.equals(Void.class)) {
			return true;
		}
		return false;
	}

	/**
	 * 快速动态调用宿主方法。 如果指定了方法名，则执行方法时只会调用指定了的方法。 <br/>
	 * 如果没有指定方法名，则调用宿主中对应接口类的同名方法。
	 * 
	 * @param superClass
	 *            接口类
	 * @param hostClass
	 *            宿主类
	 * @param methodName
	 *            方法名（可选）
	 * @return 代理实例
	 */
	public static <T> T newInvoker(Class<T> superClass, Class<?> hostClass,
			String methodName) {
		return newInvoker(superClass, hostClass, methodName, null, null);
	}

	/**
	 * 快速动态调用宿主方法。调用宿主中对应接口类的同名方法。
	 * 
	 * @param superClass
	 *            接口类
	 * @param hostClass
	 *            宿主类
	 * @return 代理实例
	 */
	public static <T> T newInvoker(Class<T> superClass, Class<?> hostClass) {
		return newInvoker(superClass, hostClass, null);
	}
}
