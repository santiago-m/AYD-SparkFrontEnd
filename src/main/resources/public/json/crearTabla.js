function tableCreate() {
            var body = document.getElementsByTagName('body')[0];
            var tbl = document.createElement('table');
            tbl.style.width = '100%';
            tbl.setAttribute('border', '1');
            var tbdy = document.createElement('tbody');
            for (var i = 0; i < 10; i++) {
                var tr = document.createElement('tr');
                for (var j = 0; j < 2; j++) {
                    if (j == 0) {
                        var td = document.createElement('td');
                        td.appendChild(document.createTextNode('Host '+(i+1)))
                        i == 1 && j == 1 ? td.setAttribute('rowSpan', '1') : null;
                    tr.appendChild(td)
                    }
                    else if (j == 1) {
                        var td = document.createElement('td');
                        td.appendChild(document.createTextNode('hostName'))
                        //i == 1 && j == 1 ? td.setAttribute('rowSpan', '2') : null;
                    tr.appendChild(td)
                    }

                    /*if (i == 2 && j == 1) {
                        break
                    } else {
                        var td = document.createElement('td');
                        td.appendChild(document.createTextNode(''))
                        i == 1 && j == 1 ? td.setAttribute('rowSpan', '2') : null;
                    tr.appendChild(td)
                    //}*/
                }
            tbdy.appendChild(tr);  
            }
            tbl.appendChild(tbdy);
            body.appendChild(tbl)
        }
       document.getElementById("hosts").innerHTML = tableCreate();
