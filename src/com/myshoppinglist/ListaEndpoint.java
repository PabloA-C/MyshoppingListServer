package com.myshoppinglist;

import com.myshoppinglist.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "listaendpoint", namespace = @ApiNamespace(ownerDomain = "myshoppinglist.com", ownerName = "myshoppinglist.com", packagePath = ""))
public class ListaEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listLista")
	public CollectionResponse<Lista> listLista(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Lista> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Lista as Lista");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Lista>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Lista obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Lista> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getLista")
	public Lista getLista(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Lista lista = null;
		try {
			lista = mgr.find(Lista.class, id);
		} finally {
			mgr.close();
		}
		return lista;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param lista the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertLista")
	public Lista insertLista(Lista lista) {
		EntityManager mgr = getEntityManager();
		try {
			mgr.persist(lista);
		} finally {
			mgr.close();
		}
		return lista;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param lista the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateLista")
	public Lista updateLista(Lista lista) {
		EntityManager mgr = getEntityManager();
		try {
			mgr.persist(lista);
		} finally {
			mgr.close();
		}
		return lista;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeLista")
	public void removeLista(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Lista lista = mgr.find(Lista.class, id);
			mgr.remove(lista);
		} finally {
			mgr.close();
		}
	}

	private boolean containsLista(Lista lista) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Lista item = mgr.find(Lista.class, lista.getKey());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
