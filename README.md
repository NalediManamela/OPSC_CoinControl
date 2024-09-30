PURPOSE OF OUR APP

The purpose of our application is to help users manage their finances.
Users are able to register with our application to create an account. Once their account is created, they can log into their account.
Once logged in, their data within our application will be displayed.

Our application is organised as follows:
1. Users can register and log in, or log in with SSO using their google account.
2. Once logged in, users can create a category, for example, Luxuries. The user can assign a budget to the category, for example R10 000,00.
3. When users click on the created category, users can add transactions, for example, Jewelry, within the category.
4. Users can keep adding transactions within the category, and each transaction will have a name, cost, and date attached to it.
5. In this way, users can keep track of all their past transactions, and what category the transaction was associated with.
6y. As transactions are added to the category, the total sum cost of the transactions are calculated and diplayed.
7. The total sum cost of the transactions are displayed next to the budget. This helps users to track if they are keeping their spending within their set budget.
8. The average cost of the transactions are diaplayed as well, to help users keep track of how much they are spending on a certain category on average.
9. The app includes a debit order screen. This helps users keep track of their monthly payments.
10. Users can add monthly payments on this screen.
11. The total of their monthly payments is calcualted, as well as the total of their monthly payments that is due for the current month.

All of this is handled through our API, which we created in Visual Studio and published to Azure.
This includeds the the adding and deletion of data to and from our SQL database, as well as the calculations.

Overall, we hope our app will help users manage and keep track of their finances and finance history.

DESIGN CONSIDERATIONS

We researched 3 similar application, and atttempted to incorporate their strengths into our application.
The apps we researched made extensive use of dialogs, views to diplay data in lists, and icons to convey information.

We designed the app to keep it simple, lightweight and user friendly.

We used images for the buttons, to convey what their functionalities are, for example, a "plus" sign to add categories and transactions.
We used recyclerViews to display categories, transactions and debit orders.
We made use of onClickListeners, to make the UI more user friendly. For example, when a user click in the category name in the recyclerView, they
are taken direcly to the transaction screen that will display the transactions for that category.
Addtionally, instead of new screens, adding transactions and debit orders are done through dialogues, to keep our application more streamlined.
We also made use of gestures such as longclicklistener, to make certain functions easier to access. For example, users can click and hold on a category name, to delete it.

Across the app, we also made sure to maintain a consistent colour scheme.
We made sure that the app will display all the UI elements correctly on different screens.
Overall, we made an effort to make our app easy to use, with good UI practices.

We made multiple commits over a period of time to a diffrent repository, before moving our application and API to this one.
We also made use of github actions and workflows to test the build of our app using a build.yms. The test proved mostly successful, with some errors stemming from
contraints and depreciated packages.
    
