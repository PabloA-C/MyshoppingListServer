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

@Api(name = "listitemendpoint", namespace = @ApiNamespace(ownerDomain = "myshoppinglist.com", ownerName = "myshoppinglist.com", packagePath = ""))
public class ListItemEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listListItem")
	public CollectionResponse<ListItem> listListItem(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<ListItem> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from ListItem as ListItem");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<ListItem>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (ListItem obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<ListItem> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getListItem")
	public ListItem getListItem(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		ListItem listitem = null;
		try {
			listitem = mgr.find(ListItem.class, id);
		} finally {
			mgr.close();
		}
		return listitem;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param listitem the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertListItem")
	public ListItem insertListItem(ListItem listitem) {
		EntityManager mgr = getEntityManager();
		try {
			mgr.persist(listitem);
		} finally {
			mgr.close();
		}
		return listitem;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param listitem the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateListItem")
	public ListItem updateListItem(ListItem listitem) {
		EntityManager mgr = getEntityManager();
		try {
			mgr.persist(listitem);
		} finally {
			mgr.close();
		}
		return listitem;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeListItem")
	public void removeListItem(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			ListItem listitem = mgr.find(ListItem.class, id);
			mgr.remove(listitem);
		} finally {
			mgr.close();
		}
	}

	private boolean containsListItem(ListItem listitem) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			ListItem item = mgr.find(ListItem.class, listitem.getKey());
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
