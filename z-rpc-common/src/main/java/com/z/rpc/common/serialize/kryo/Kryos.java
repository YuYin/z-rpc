
package com.z.rpc.common.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.EnumNameSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

import java.io.InputStream;
import java.util.Arrays;

/**
 * kryo序列化工具类
 * 其他实现:
 *  @see
 *  <ul>
 *   org.apache.dubbo.common.serialize.kryo.utils.ThreadLocalKryoFactory
 *   org.apache.dubbo.common.serialize.kryo.utils.PooledKryoFactory
 *   org.apache.dubbo.common.serialize.kryo.KryoSerialization dubbo kryo序列化实现
 *  </ul>
 *
 */
public class Kryos {

	public static final int MAX_BUFFER_SIZE = -1;

	public static final int INITIAL_BUFFER_SIZE = 1024 * 2;

	private static ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new ReflectionFactorySupportKryo();
			kryo.setDefaultSerializer(DuplicateFieldNameAcceptedCompatibleFieldSerializer.class);
			//注册kryo不能序列化的类
			kryo.register(Arrays.asList("a").getClass(), new JavaSerializer());
			//增加UnmodifiableCollectionsSerialize，解决当类中有反序列化字段类型为UnmodifiableList时 反序列化报错：not supported 2020.02.26
			UnmodifiableCollectionsSerializer.registerSerializers(kryo);

			kryo.addDefaultSerializer(Enum.class, EnumNameSerializer.class);
			return kryo;
		}
	};

	private static ThreadLocal<Output> outputThreadLocal = new ThreadLocal<Output>() {
		protected Output initialValue() {
			return new Output(Kryos.INITIAL_BUFFER_SIZE, Kryos.MAX_BUFFER_SIZE);
		}
	};

//	static {
//		ShutdownHooks.addShutdownHook(new Runnable() {
//			@Override
//			public void run() {
//				if (kryoThreadLocal != null) {
//					kryoThreadLocal.remove();
//				}
//				if (outputThreadLocal != null) {
//					outputThreadLocal.remove();
//				}
//			}
//		}, "KryosShutdownHook");
//	}

	public static Kryo getKryo() {
		return kryoThreadLocal.get();
	}

	public static Output getOutput() {
		return outputThreadLocal.get();
	}

	public static <T> byte[] writeObject(T object) {
		Kryo kryo = getKryo();
		Output output = getOutput();
		try {
			kryo.writeObject(output, object);
			return output.toBytes();
		} finally {
			output.clear();
		}
	}

	public static <T> T readObject(byte[] in, Class<T> toClass) {
		if (in == null) {
			return null;
		}
		Kryo kryo = getKryo();
		Input input = new Input();
		input.setBuffer(in);
		T result = kryo.readObject(input, toClass);
		return result;
	}

	public static byte[] serialize(Object o) {
		Kryo kryo = getKryo();
		Output output = getOutput();
		try {
			kryo.writeClassAndObject(output, o);
			return output.toBytes();
		} finally {
			//注意:此处不能使用output.close(),因为没有使用OutputStream流,关闭的时候流为空，不会重置position
			output.clear();
		}

	}

	public static <T> T deserialize(byte[] in, Class<T> toClass) {
		if (in == null) {
			return null;
		}
		Kryo kryo = getKryo();
		Input input = new Input();
		input.setBuffer(in);
		Object result = kryo.readClassAndObject(input);
		return toClass.cast(result);
	}

	public static Object deserialize(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}
		Kryo kryo = getKryo();
		Input input = new Input(inputStream);
		return kryo.readClassAndObject(input);
	}

	public static Object deserialize(byte[] in) {
		if (in == null) {
			return null;
		}
		Kryo kryo = getKryo();
		Input input = new Input();
		input.setBuffer(in);
		return kryo.readClassAndObject(input);
	}
}
