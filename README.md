
# Rob's Picnic Project

This is my Picnic project.

## Description, caveats and disclaimers:

1. The app consists of two activities, the `ListActivity` and the `DetailActivity`.

2. The `ListActivity` loads a list of product information and displays the returned values using a RecyclerView. When one of the items is clicked, the `DetailActivity` is started, the product ID is sent to it using the Intent extras, and then the `DetailActivity` loads the product data from the API and displays it.

3. Both activities extend a `BaseActivity` class, which contains some common methods.

4. An `Asynctask` is used in order to make the API calls on a separate thread. Each activity class has an inner class which extends `BaseDownloadAsynctask` (which is an inner class of `BaseActivity`).

5. The `Asynctask` will survive resource changes such as those caused by screen rotation. If the data had already been downloaded, then it will be preserved using the `onSaveInstanceState` method.

6. In order to show basic support for different configurations, the app displays strings in Dutch if the user's locale is Dutch.

7. [Retrofit](square.github.io/retrofit/) is used for the API calls.

8. [Picasso](square.github.io/picasso/) is used for image handling.

9. The app contains a simple attempt at making the UI a bit more user-friendly. A 9-patch image is used as a background for the price to mimick a price tag.


## Improvements

This was meant to be a short programming exercise, so just the basic functionality was built in. Possible improvements include:

1. Adding tests.

2. Supporting different devices, locales, etc. This includes designing layouts according to device characteristics, supporting more languages, etc.

3. Using fragments (also useful for the previous point). In the current implementation two activities were used instead of fragments because that's what the project description requested.

4. Adding elements to support navigation, such as a navigation drawer.

5. Data caching in order to avoid hitting the API frequently.


