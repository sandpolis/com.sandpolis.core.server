package com.sandpolis.core.server.state;

import java.util.List;
import java.util.function.Supplier;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;
import com.sandpolis.core.instance.State.ProtoSTObjectUpdate;
import com.sandpolis.core.instance.state.oid.Oid;
import com.sandpolis.core.instance.state.st.EphemeralAttribute.EphemeralAttributeValue;
import com.sandpolis.core.instance.state.st.STAttribute;
import com.sandpolis.core.instance.state.st.STDocument;

public class PersistentAttribute implements STAttribute {

	private STAttribute container;

	private Document mongoDocument;

	public PersistentAttribute(STAttribute container, MongoDatabase database) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addListener(Object listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Oid oid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STDocument parent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeListener(Object listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<EphemeralAttributeValue> history() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get() {
		return mongoDocument.get("value");
	}

	@Override
	public void set(Object value) {
		mongoDocument.put("value", value);
	}

	@Override
	public void source(Supplier<?> source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long timestamp() {
		return mongoDocument.getInteger("timestamp");
	}

	@Override
	public void merge(ProtoSTObjectUpdate snapshot) {
		// TODO Auto-generated method stub

	}

	@Override
	public ProtoSTObjectUpdate snapshot(Oid... oids) {
		// TODO Auto-generated method stub
		return null;
	}

}
