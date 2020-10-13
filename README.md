

Example of ecommerce checkout step implemented with event sourcing.

Using events allow us track all user activity 
1. customer land on checkout with selected product  ( wishlist step )
2. user enter payment detail, delivery info and click "buy"
3. system charge the user
4. system buy what user want
5. system schedule the product delivery
6. system sent a notification receipt to the customer

For any reason the checkout process can be interrupted in any step 
but it can be resumed retrieving the events to previous steps.

The checkout state is not store is calculated by events.

Having events stored is possible to provide different read models for different users.
For example 
- data analytics user may need a view that summarize the total by product type.
- customer may need to see what is the status of his checkout
