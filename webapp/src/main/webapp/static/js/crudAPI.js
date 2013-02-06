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
            $("#allHead").append("<th>" + key + "</th>");
            
            // Create
            $("#createFieldset").append("<label for='create_" + key + "'>" + key + "</label>" +
                "<input id='create_" + key + "' name='" + key + "' type='" + item + "' /><br/>");
            if ("createdBy" == key || "updatedBy" == key || "createdDate" == key || "updatedDate" == key) {
                $("#create_" + key).attr("disabled", "disabled");
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
        val = $("#create_" + key).val();
        console.log("   inspecting " + key + " with value " + val);
        if ("createdBy" == key || "updatedBy" == key || "createdDate" == key || "updatedDate" == key) {
            // do not map
        }
        else {
            body[key] = val;
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
        primaryKey + "</td></tr>");
    var value;
    $.map(schema.columns, function(clazz, key) {
        value = item[key];
        console.log("   adding td for " + value);
        $("#all_" + primaryKey).append("<td>" + value + "</td>");
    });
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