
package com.z.rpc.common.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.minlog.Log;

import java.util.*;

/**
 * <p>
 *     1.CompatibleFieldSerializer自定义class(比如TestDto,SingleRequest)序列化器,兼容增加字段和减少字段,举例:假如某个dto增加字段或者减少字段，对序列化不影响
 *     2.kryo内部定义了很多序列化器,比如基本类型的序列化器,字符类型的序列化器,枚举类型的序列化器，集合类型的序列化器
 *     3.kryo序列化时同时会将类的全名序列化
 * </p>
 * @param <T>
 */
public class DuplicateFieldNameAcceptedCompatibleFieldSerializer<T> extends CompatibleFieldSerializer<T> {
	
	private CachedField[] fields;
	
	public DuplicateFieldNameAcceptedCompatibleFieldSerializer(Kryo kryo, Class type) {
		super(kryo, type);
	}
	
	protected void initializeCachedFields() {
		//get all cached fields form super class
		CachedField[] fields = super.getFields();
		boolean found = false;
		Map<String, CachedField> cachedFiledMap = new HashMap<String, CachedField>();
		for (CachedField field : fields) {
			String fieldName = field.getField().getName();
			if (!cachedFiledMap.containsKey(fieldName)) {
				cachedFiledMap.put(fieldName, field);
			} else {
				//field belong to this.getType() will be save to cachedFiledMap
				if (field.getField().getDeclaringClass().equals(this.getType())) {
					cachedFiledMap.put(fieldName, field);
				} else {
					Log.warn(field.getField().getDeclaringClass()	+ "和" + this.getType() + "中都存在[field=" + fieldName
								+ "],请去掉" + this.getType() + "中的" + fieldName);
					//field belong to super class will be ignored
					found = true;
				}
			}
		}
		if (!found) {
			setFields(fields);
		} else {
			List<CachedField> list = new ArrayList<>(cachedFiledMap.values());
			Collections.sort(list, this);
			//set cached fields, the fields order must be as before
			setFields(list.toArray(new CachedField[cachedFiledMap.size()]));
		}
	}
	
	public void removeField(String fieldName) {
		for (int i = 0; i < fields.length; i++) {
			CachedField cachedField = fields[i];
			if (cachedField.getField().getName().equals(fieldName)) {
				CachedField[] newFields = new CachedField[fields.length - 1];
				System.arraycopy(fields, 0, newFields, 0, i);
				System.arraycopy(fields, i + 1, newFields, i, newFields.length - i);
				fields = newFields;
				super.removeField(fieldName);
				return;
			}
		}
		throw new IllegalArgumentException("Field \"" + fieldName + "\" not found on class: " + getType().getName());
	}
	
	public CachedField[] getFields() {
		return fields;
	}
	
	public void setFields(CachedField[] fields) {
		this.fields = fields;
	}
	
}
