# Adverity assignment solution

Here it is Kotlin/SpringBoot solution for given assigment. Main idea is to use SpringEL as a parser for expression and calculate group-by, 
filter, and fields values dinamically by SpringEL framework.

## Example queries
* Clicks per `datasource` for a given range
 `http://localhost:8080/clicks/groups/datasource/fields/sum('clicks')?from=10/10/2019&to=11/10/2019`
* CTR per `datasource` and `campaign` for a given range
`http://localhost:8080/clicks/groups/datasource,campaign/fields/1.0*sum('clicks')%2Fsum('impressions')?from=10/10/2019&to=11/10/2019`
## Assumptions* 
