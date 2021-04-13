package com.sandpolis.core.server.state;

import java.util.Collection;
import java.util.function.Consumer;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;
import com.sandpolis.core.instance.State.ProtoSTObjectUpdate;
import com.sandpolis.core.instance.state.oid.Oid;
import com.sandpolis.core.instance.state.st.STAttribute;
import com.sandpolis.core.instance.state.st.STDocument;

public class PersistentDocument implements STDocument {

	private STAttribute container;

	private Document mongoDocument;

	public PersistentDocument(STDocument container, MongoDatabase database) {
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
	public STAttribute attribute(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int attributeCount() {
		return mongoDocument.size();
	}

	@Override
	public Collection<STAttribute> attributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STDocument document(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int documentCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<STDocument> documents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEachAttribute(Consumer<STAttribute> consumer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forEachDocument(Consumer<STDocument> consumer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(STAttribute attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(STDocument document) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub

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

	@Override
	public void set(String id, STAttribute attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(String id, STDocument document) {
		// TODO Auto-generated method stub

	}

}
