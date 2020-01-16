Microservice for running R-scripts in forecast models.

Requires R installed on the host.

For executing a script send it as a body of POST request to URL
```
/<result_type>?result=<result_name>
```
Supported result types:

    * double_array

Output format: csv
