(function($,W,D){

    var jvmList = new Array();

    $.addJvmToList = function(jvm)
    {
        jvmList.push(jvm)
    }

    var isJvmListed = function(vmid)
    {
        if($("#jvm-list tr").length===0)
        {
            return false;
        }
        else
        {
            return $("#jvm-list tr").find("input").attr("data-id") === vmid;
        }
    }

    $.updateJvmList = function()
    {
        $.getJSON("/jvm-monitoring-server/api/vms/?since=0", function(jvms) {
            $("#jvm-list").find("tr").remove();
            $.each(jvms, function(idx, jvm){
                if(!isJvmListed(jvm.id))
                {
                    $("#jvm-list").append(buildJvmRow(jvm));
                }
            });
        });
    }

    $.handleJvmCheckbox = function(checkbox)
    {
        var vmid = $(checkbox).attr("data-id");
        if($(checkbox).is(':checked'))
        {
            var vmname = $(checkbox).attr("data-name");
            $.addChartForJvm(vmid, vmname);
        }
        else
        {
            $.delChartForJvm(vmid);
        }

    }

    var buildJvmRow = function(jvm){
        var checked = $.isChartForJvmDisplayed(jvm.id)?"checked":"";
        var jvmRow = '<tr>'
                   + '    <td><input type="checkbox" data-name="'+jvm.name+'" data-id="'+jvm.id+'" onchange="$.handleJvmCheckbox(this)" '+checked+'></td>'
                   + '    <td><div class="id">'+jvm.id+'</div></td>'
                   + '    <td><div class="name">'+jvm.name+'</div></td>'
                   + '    <td><div class="options">'+jvm.options+'</div></td>'
                   + '</tr>'

        return jvmRow;
    };


    $(document).ready(function( ){

    });

})(jQuery, window, document);


