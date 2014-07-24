<%@include file="header.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<style type="text/css">
body{
	background-color: #888;
	color:white;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
	font-size: 22px;
}

.lg{
	
}
.mail{
	color:yellow;
}
.lync{
	color:green;
}
#prolog{
	background-color: #888;
	width: 900px;
	font-size: 28px;
}
#gallery {
		background-color: #888;
		padding: 10px;
		width: 520px;
	}
	#gallery ul { list-style: none; }
	/*#gallery ul li { display: inline; }*/
	#gallery ul img {
		border: 3px solid #3e3e3e;
		border-width: 3px 3px 20px;
	}
	#gallery ul a:hover img {
		border: 5px solid #fff;
	border-width: 5px 5px 20px;
	color: #fff;
	}
	#gallery ul a:hover { color: #fff; }

</style>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery.lightbox-0.5.js"></script>
<link rel="stylesheet" type="text/css" href="css/jquery.lightbox-0.5.css" media="screen" />

<script type="text/javascript">
$(function() {
	// Use this example, or...
	//$('a[@rel*=lightbox]').lightBox(); // Select all links that contains lightbox in the attribute rel
	 $('#gallery .lg').lightBox();
	// This, or...
	//$('#gallery a').lightBox(); // Select all links in object with gallery ID
	// This, or...
	//$('a.lightbox').lightBox(); // Select all links with lightbox class
	// This, or...
	//$('a').lightBox(); // Select all links in the page
	// ... The possibility are many. Use your creative or choose one in the examples above
});
</script>
</head>
<body>

<div id="gallery">
	<div id="prolog">We provide entire BlackBox Automation Test Solution, from hardware to software.<br/></div>
	Contact List: 
	 <ul>
        <li>
            <a class="lg" href="images/jeffery_big.png" title=" Jeffery, Austere Product Owner, Senior Architect" >
                <img alt="" src="images/jeffery.png"/> Jeffery Zhao  
            </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <a class="lync" href="im:sip:jeffery.zhao@nokia.com">Lync Him</a>&nbsp;&nbsp;&nbsp;
            <a class="mail" href="mailto:jeffery.zhao@nokia.com?Subject=Austere Question">Mail Him</a>
        </li>
        <li>
            <a class="lg" href="images/evan_big.png" title="Evan, Austere Main Developer, Senior Engineer.">
                <img alt="" src="images/evan.png"/>Evan Chen 
            </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <a class="lync" href="im:sip:evan.1.chen@nokia.com">Lync Him</a>&nbsp;&nbsp;&nbsp;
            <a class="mail" href="mailto:evan.1.chen@nokia.com?Subject=Austere Question">Mail Him</a>
        </li>
        <li>
            <a class="lg" href="images/yangdi.jpg" title="Yang Di, Austere Execution Manager">
                <img alt="" src="images/yangdi.jpg"/>Yang Di
            </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <a class="lync" href="im:sip:di.6.yang@nokia.com">Lync Her</a>&nbsp;&nbsp;&nbsp;
            <a class="mail" href="mailto:di.6.yang@nokia.com?Subject=Austere Question">Mail Her</a>
            
        </li>
    </ul>
</div>
</body>
</html>