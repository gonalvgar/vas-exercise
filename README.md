# vas-exercise
 Here's my solution to the test provided by vas.

I've implemented a really simple GUI to make easier to test the methods contained in each endpoint, you can access them by adding "/view" after the metrics/kpis endpoints deployed in local (port 8080), anyway, you can just access the endpoint as required so the app will return you a String in json format of all the information collected by the methods.

To make the service work, you just have to add /date/YYYYMMDD to process the file you want, the app will make a request to the api, reformat the json to make the parse to java object work, and then, save the data into the h2 database included by springboot. The service expects you to introduce a badly formed url, or to receive the json in a incorrect format, so it'll return an exception with the problem.
Everytime you request to process a file, the system will wipe the whole database, and then insert the requested data, to make sure that we're only analyzing the data provided by the api.

Also, I've commented almost every class describing what I'm doing and why.
