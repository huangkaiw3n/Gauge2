package org.gauge.resources;

/**
 * Created by Kaiwen on 12/4/2015.
 */
public class Registration {
    public final static String registration =
            "    <!DOCTYPE html>\n" +
            "    <html>\n" +
            "    <head>\n" +
            "    <title>Test submit form</title>\n" +
            "    <!--<script language=\"JavaScript\" type=\"text/javascript\" src=\"js/voucher.js\"></script>-->\n" +
            "    <script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>\n" +
            "    <meta charset=\"utf-8\" />\n" +
            "    </head>\n" +
            "    <body>\n" +
            "    <p style=\"font-size:30px\">Gauge Client Registration</p>\n" +
            "    Registered Users: <span id=\"totalUsers\">0</span>\n" +
            "    Online Users: <span id=\"onlineUsers\">0</span>\n" +
            "    <form id=\"loginForm\" name=\"form\" action=\"register\">\n" +
            "    <div class=\"form-item\">\n" +
            "    <label>Username:</label>\n" +
            "    <input type=\"text\" id=\"username\" name=\"username\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <div class=\"form-item\">\n" +
            "    <label>Password: </label>\n" +
            "    <input id=\"password\" name=\"password\" type=\"password\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <div class=\"form-item\">\n" +
            "    <label>Email:    </label>\n" +
            "    <input id=\"email\" name=\"email\" type=\"text\" maxlength = \"30\"/>\n" +
            "    </div>\n" +
            "    <input type=\"submit\" name=\"submit\" value=\"Submit\" onClick='getData()'/>\n" +
            "    </form>\n" +
            "    </body>\n" +
            "    <script>\n" +
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
            "                    url: \"/userStats?\",\n" +
            "                    type: \"GET\",\n" +
            "                    crossDomain: true,\n" +
            "                    data: null,\n" +
            "                    dataType: \"json\",\n" +
            "                    success: function(data) {\n" +
            "                $('#totalUsers').html(data.totalUsers + \"\");\n" +
            "                $('#onlineUsers').html(data.onlineUsers + \"\");\n" +
            "            }\n" +
            "            });\n" +
            "        }, 1000);\n" +
            "    });\n" +
            "    </script>\n" +
            "    </html>";

}
