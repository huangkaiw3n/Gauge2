package org.gauge.resources;

/**
 * Created by Kaiwen on 12/4/2015.
 */
public class Registration {
    public final static String registration = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Test submit form</title>\n" +
            "    <!--<script language=\"JavaScript\" type=\"text/javascript\" src=\"js/voucher.js\"></script>-->\n" +
            "    <script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>\n" +
            "   <link rel='stylesheet' href='http://cdnjs.cloudflare.com/ajax/libs/semantic-ui/1.1.2/semantic.min.css'/>\n" +
            "    <meta charset=\"utf-8\" />\n" +
            "<style>\n" +
            "  .container {\n" +
            "  margin: auto;\n" +
            "  width: 100%;\n" +
            "  height: 100%;\n" +
            "  background-color: red;\n" +
            "  background-image: url(\"http://cdn.backgroundhost.com/backgrounds/subtlepatterns/batthern.png\")\n" +
            "}\n" +
            "\n" +
            ".container-inner {\n" +
            "  margin: auto;\n" +
            "  position: relative;\n" +
            "  right: 0;\n" +
            "  display: table;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div class=\"container\">\n" +
            "    <div class=\"container-inner\">\n" +
            "    <div class=\"ui hidden divider\"></div>  \n" +
            "    <div class=\"ui center aligned segment card\">\n" +
            "<p style=\"font-size:30px\">Gauge Client Registration</p>\n" +
            "      <div class=\"ui statistic\">\n" +
            "        <div class=\"value\"><span id=\"totalUsers\">0</span></div>\n" +
            "        <div class=\"label\">Registered</div>\n" +
            "      </div>\n" +
            "      <div class=\"ui statistic\">\n" +
            "        <div class=\"value\"><span id=\"onlineUsers\">0</span></div>\n" +
            "        <div class=\"label\">Online</div>\n" +
            "      </div>\n" +
            "<form id=\"loginForm\" name=\"form\" action=\"register\">\n" +
            "    <div class=\"form-item ui labeled input\">\n" +
            "        <label class=\"ui label\">Username:</label>\n" +
            "        <input type=\"text\" placeholder=\"username\" id=\"username\" name=\"username\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <div class=\"ui hidden divider\"></div>  \n" +
            "    <div class=\"form-item ui labeled input\">\n" +
            "        <label class=\"ui label\">Password: </label>\n" +
            "        <input id=\"password\"  placeholder=\"password\" name=\"password\" type=\"password\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <div class=\"ui hidden divider\"></div>  \n" +
            "    <div class=\"form-item ui labeled input\">\n" +
            "        <label class=\"ui label\">Email:    </label>\n" +
            "        <input id=\"email\"  placeholder=\"email\" name=\"email\" type=\"text\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <div class=\"ui hidden divider\"></div>  \n" +
            "    <input class=\"ui blue button\" type=\"submit\" name=\"submit\" value=\"Submit\" onClick='getData()'/>\n" +
            "  <div>\n" +
            "  </div>\n" +
            "</form>\n" +
            " </div>\n" +
            " </div>\n" +
            "  </div>\n" +
            "</body>\n" +
            "<script>\n" +
            "    $(document).ready(function () {\n" +
            "        document.getData = function () {\n" +
            "            var $params = $('#loginForm :input');\n" +
            "            var jsonObj = {};\n" +
            "            $params.each(function () {\n" +
            "                jsonObj[this.name] = $(this).val();\n" +
            "            });\n" +
            "            console.log(jsonObj);\n" +
            "\n" +
            "            // perform actual GET request with jsonObj as payload\n" +
            "            $.get('',jsonObj, function(data) {\n" +
            "                console.log('Received reply: ' + data);\n" +
            "            }, 'json');\n" +
            "        };\n" +
            "        document.numberUsers = 0;\n" +
            "        setInterval(function() {\n" +
            "            $.ajax({\n" +
            "\t    \turl: \"http://172.28.181.61/userStats?\",\n" +
            "\t    \ttype: \"GET\",\n" +
            "\t    \tcrossDomain: true,\n" +
            "\t    \tdata: null,\n" +
            "\t    \tdataType: \"json\",\n" +
            "\t    \tsuccess: function(data) {\n" +
            "\t    \t\tconsole.log(data.totalUsers);\n" +
            "\t\t\t$('#totalUsers').html(data.totalUsers + \"\");\n" +
            "\t\t\t$('#onlineUsers').html(data.onlineUsers + \"\");\n" +
            "\t\t}\n" +
            "            });\n" +
            "        }, 1000);\n" +
            "    });\n" +
            "</script>\n" +
            "</html>";
}
