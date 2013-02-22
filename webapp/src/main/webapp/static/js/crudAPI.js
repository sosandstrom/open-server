function crudLoadSchema() {
    $.getJSON("schema", {})
    .success(function(data, statusText, jqXHR) {
        $("#create-entity").data("schema", data);
        $("title").text(data.tableName);
        $("#tableName").text(data.tableName);
        
        // All
        $("#allHead").empty();
        $("#allHead").append("<th>" + data.primaryKeyName + "</th>");
        
        // Create
        $("#createFieldset").append("<label for='create_" + data.primaryKeyName + "'>" + data.primaryKeyName + "</label>" +
            "<input id='create_" + data.primaryKeyName + "' name='" + data.primaryKeyName + "' type='" + data.primaryKeyType + 
            "' placeholder='primary key' /><br/>");
        if ("number" == data.primaryKeyType) {
            $("#create_" + data.primaryKeyName).attr("disabled", "disabled");
        }
        
        $.map(data.columns, function(item, key) {
            if (!crudIsAuditField(key)) {
                $("#allHead").append("<th>" + key + "</th>");
            }
            
            // Create
            if (!crudIsAuditField(key)) {
                $("#createFieldset").append("<label for='create_" + key + "'>" + key + "</label>" +
                    "<input id='create_" + key + "' name='" + key + "' type='" + item + "' /><br/>");
            }
        });
    })
}

function crudCreateEntity() {
    console.log("Building entity...");
    var body = {};
    var schema = $("#create-entity").data("schema");
    var val;
    
    // add primary key?
    if ("text" == schema.primaryKeyType) {
        val = $("#create_" + schema.primaryKeyName).val();
        if (val && 0 < val.length()) {
            body[schema.primaryKeyName] = val;
        }
    }
    
    // map properties
    $.map(schema.columns, function(item, key) {
        if (crudIsAuditField(key)) {
            // do not map
        }
        else {
            body[key] = $("#create_" + key).val();
        }
    });
    
    crudCreate(body, function(data, statusText, jqXHR) {
        document.getElementById("createForm").reset();
    });
}

function crudCreate(body, successFunction) {
    $.post("../v10", body)
    .success(successFunction);
}

function crudAddEntity(item, index, schema) {
    var primaryKey = item[schema.primaryKeyName];
    $("#allBody").append("<tr id='all_" + primaryKey + "' ><td>" +
        "<label class='checkbox'><input type='checkbox' id='all_" + primaryKey + "_pk'/>" +
        primaryKey + "</label></td></tr>");
    var value;
    var title = "";
    $.map(schema.columns, function(clazz, key) {
        value = item[key];
        if ("date" == clazz) {
            value = crudFormatMillis(value);
        }
        if (crudIsAuditField(key)) {
            title = title + key + " " + value + ", ";
        }
        else {
            $("#all_" + primaryKey).append("<td>" + value + "</td>");
        }
    });
    $("#all_" + primaryKey).attr('title', title);
}

function crudFormatMillis(millis) {
    var d = new Date(millis);
//    return d.toUTCString();
    return d.toLocaleString();
}

function crudIsAuditField(key) {
   return "createdBy" == key || "createdDate" == key || "updatedBy" == key || "updatedDate" == key; 
}

function crudLoadMore() {
    $("#allBody").empty();
    var body = {};
    var schema = $("#create-entity").data("schema");
    crudGetPage(body, function(data, statusText, jqXHR) {
        $.map(data.items, function(item, index) {
            crudAddEntity(item, index, schema);
        });
    });
}

function crudGetPage(body, successFunction) {
    $.getJSON("../v10", body)
    .success(successFunction);
}