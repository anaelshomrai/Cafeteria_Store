package com.cafeteria.cafeteria_store.data;

import java.io.Serializable;

/**
 *
 * @author Shira Elitzur
 *
 * An instance of this class represent an ordered item from a specific order
 *
 */
public class OrderedItem implements Serializable {


    /**
     * The id of this ordered item (auto generated by db)
     */
    private int id;

    /**
     * The item in the menu that this ordered item is an actual instance of
     *
     */
    private Item parentItem;

    /**
     * The customer can attach comment to the ordered item
     */
    private String comment;

//	/**
//	 * The parent-order of this ordered item
//	 */
//	@ManyToOne // Bidirectional ManyToOne relationship ( Order has a list of OrderItem )
//	@JoinColumn( name = "Order_Id" )
//	private Order order;

    public OrderedItem() {
        setParentItem(new Item());
    }

    /**
     * Returns the id of this ordered item
     * @return the id of this ordered item
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this ordered item
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the Item that is this orderer-item parent
     * @return the parent item
     */
    public Item getParentItem() {
        return parentItem;
    }

    /**
     * Sets item as parent item for this ordered item
     * @param parentItem
     */
    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    /**
     * Returns the comment that the customer attached to the item
     * @return comment that was attached to the item
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets a comment to the ordered item ( customer comment. example:
     * "Not too hot")
     * @param comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

//	/**
//	 * Returns the order object of this ordered item
//	 * @return the order object of the item
//	 */
//	public Order getOrder() {
//		return order;
//	}
//
//	/**
//	 * Sets order object to this item
//	 * @param order
//	 */
//	public void setOrder(Order order) {
//		this.order = order;
//	}

}
