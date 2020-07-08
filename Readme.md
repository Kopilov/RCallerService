Microservice for running R-scripts in forecast models.

Requires R installed on the host.

For executing a script send it as a body of POST request to URL
```
/<result_type>?result=<result_name>
```
Supported result types:

    * double_array
    * text_array
    * text

Output format: csv

Example

Start the service (in Docker by `docker run -it --net=host kopilov/rcallerservice:1.0.0` or directly on the system by `java -jar rcallerservice.jar`)

Prepare the script such as
```
v <- 1:9;
v * 10 -> u;
```
Put it in file named like `example.r`
    
Run the query: `curl --data-binary @example.r http://127.0.0.1:8080/double_array?result=u`  
The output should be `10.0;20.0;30.0;40.0;50.0;60.0;70.0;80.0;90.0`
