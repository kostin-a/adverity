# Adverity assignment solution

Here it is Kotlin/SpringBoot solution for given assigment. Main idea is to use SpringEL as a parser for expression and calculate group-by, 
filter, and fields values dinamically by SpringEL framework.
## Usage and main syntax
Central object of the model is:

```kotlin
data class ClickRow (

    var id: Long?,
    var datasource: String,
    var campaign: String,

    var daily: LocalDate,
    var clicks: Int,
    var impressions: Int
)
```

Each request has the form:

`http://localhost:8080/clicks/groups/GROUPS/fields/FIELDS[/filters/FILTERS]?from=FROM_DATE&to=TO_DATE`
where:
* `GROUPS` -- is a comma separated of fields to group for `ClickRow` objects. For example, `datasource,campaign`
* `FIELDS` -- is a comma separated of expressions that will be calculated on top of the groups: 
* * `sum('field_name')` -- for a given group calculates sum for the field, 
* * `f('field_name')` -- for a given group it takes first element in the group and field value of this object
* `/filters/FILTERS` -- optional section of comma separated expressions that will be calculated for each `ClickRow` object. For example, `clicks < 100`
* `FROM_DATE`, `TO_DATE` -- optionals parameters date start date end (inclusive)


## Example queries
* Clicks per `datasource` for a given range
 http://localhost:8080/clicks/groups/datasource/fields/sum('clicks')?from=10/10/2019&to=11/10/2019
* CTR per `datasource` and `campaign` for a given range
http://localhost:8080/clicks/groups/datasource,campaign/fields/1.0*sum('clicks')%2Fsum('impressions')?from=10/10/2019&to=11/10/2019
* `Impressions` over time daily 
http://localhost:8080/clicks/groups/daily/fields/sum('impressions')?from=10/10/2019&to=11/10/2019
## Assumptions
* for non-grouping queries, please use Identity group: `id`. For production code, we need to add special handling of non-grouping queries
* when field A requested that not presented in group clause, we return first element. In future, to avoid misleadings, we need to return error or warning in this case
