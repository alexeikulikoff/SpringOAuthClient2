$(document).ready(function(){
  $("#login-with").click(function(){
   
	  console.log('application');
    
    $.get( "/oauth", function( data ) {
    	  console.log(data);
    	 // location.href = "/hello";
    });
  });
});