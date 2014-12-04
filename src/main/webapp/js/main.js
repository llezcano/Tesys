/***************************/
/***********MAIN************/
/***************************/

getSkills('#skills') ;
getMetrics('#metrics') ;
getUsers('#users') ;
//Busco los issues del primer item seleccionado
getIssuesByUser($('#users').find('option:selected').val(), '#issues') ;


var skillPlotter ; 
var metricPlotter;
