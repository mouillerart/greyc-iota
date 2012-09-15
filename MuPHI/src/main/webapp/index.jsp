<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="fr.unicaen.iota.simulator.server.model.PipeContainer"%>
<%@page import="fr.unicaen.iota.simulator.server.model.PlaceFIFO"%>
<%@page import="fr.unicaen.iota.simulator.server.util.Configuration"%>
<%@page import="java.util.Map"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/style.css" />
        <script type='text/javascript' src='js/jquery.js'></script>
        <script type='text/javascript' src='js/jqueryprogressbar.js'></script>
        <title>Simulator Pipe Controler</title>
    </head>
    <body>
        <script type="text/javascript">
            function update(pipeName,value,remainingTravel){
                $("#"+pipeName+"_progressbar").reportprogress(value);
                if(remainingTravel == "null"){
                    $("#"+pipeName+"_remainingTravel").html("truck loading ...");
                } else{
                    $("#"+pipeName+"_remainingTravel").html("arrival in : "+remainingTravel+" ms");
                }
            }

            function init_app(){
                get_Xhr();
                xhr.onreadystatechange = function(){
                    if(xhr.readyState == 4 && xhr.status == 200) {
                        alert("Initialization complete");
                    }
                }
                xhr.open("GET",'InitApp',true);
                xhr.send(null);
            }

            var xhr = null;
            //Créons une fonction de création d'objet XMLHttRequest
            function get_Xhr() {
                if(window.XMLHttpRequest){xhr = new XMLHttpRequest();}
                else if(window.ActiveXOject){try {xhr = new ActiveXObject("Msxml2.XMLHTTP");}
                    catch(e) {try {xhr = new ActiveXObject("Microsoft.XMLHTTP");}
                        catch(el){xhr = null;}
                    }
                }
                else {alert("Votre navigateur ne supporte pas les objets XMLHTTPRequest\nVeuillez le mettre à jour");}
                return xhr;
            }


            function ajaxmonitor(){
                // Creation de l'objet XMLHttpRequest
                get_Xhr();
                xhr.onreadystatechange = function(){
                    if(xhr.readyState == 4 && xhr.status == 200) {
                        var xml = xhr.responseXML;
                        for (i=0 ; i<xml.getElementsByTagName('pipe').length ; i++){
                            var pipe = xml.getElementsByTagName('pipe')[i];
                            var name = pipe.getElementsByTagName('name')[0].firstChild.nodeValue;
                            var value = pipe.getElementsByTagName('value')[0].firstChild.nodeValue;
                            var remainingTravel = pipe.getElementsByTagName('remainingTravel')[0].firstChild.nodeValue;
                            update(name, value,remainingTravel);
                            setTimeout("ajaxmonitor()",<%=Configuration.PULL_WINDOW%>);
                        }
                        
                    }
                }
                xhr.open("GET",'PipeMonitor',true);
                //xhr.setRequestHeader('Content-Type','x-www-form-urlencoded');
                xhr.send(null);
            }

            setTimeout("ajaxmonitor()",<%=Configuration.PULL_WINDOW%>);

        </script>
        <div class="title"><div class="title_text"><h1>Simulator Pipe Controler</h1></div><div class="title_img"><img src="images/logo.png"/></div></div>
        <div><input type="button" onclick="init_app();" value="INITIALIZE PIPES" /> <input type="button" onclick="save_conf();" value="SAVE CONFIGURATION" /> </div>
        <div class="addPipe"><img style="float:left;margin-right:5px;" src="images/add.png" alt="clear pipe"/> add pipe</div>
        <table class="pipeTable">
            <tr>
                <th class="left">Pipe Name</th>
                <th>Pipe Container</th>
                <th>&nbsp;</th>
                <th  class="right">Process</th>
            </tr>

            <%
                        Map<String, PlaceFIFO> pipes = PipeContainer.getInstance().getPipes();
                        int i = 0;
                        for (String k : pipes.keySet()) {

            %>

            <tr class="pipeLine">
                <td class="pipeName"><%=k%></td>
                <td class="pipeContainer pipecell"><div class="progressbar" id="<%=k%>_progressbar"></div></td>
                <td class="pipeAction">
                    <div class="for_pics">&nbsp;</div>
                    <div class="pics"><img src="images/trash.png" alt="clear pipe"/></div>
                    <div class="pics"><img src="images/edit.png" alt="edit pipe"/></div>
                    <div class="pics"><img src="images/delete.gif" alt="delete pipe"/></div>
                </td>
                <td class="pipe_travel" id="<%=k%>_remainingTravel">none</td>
            </tr>
            <%

                        }
            %>
            <tr>
                <th class="left">&nbsp;</th>
                <th>&nbsp</th>
                <th>&nbsp;</th>
                <th  class="right">&nbsp;</th>
            </tr>
        </table>
    </body>
</html>
