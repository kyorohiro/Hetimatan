
#!/bin/sh

echo $0
pat=$1

case "$pat" in
"client") 
java -cp hetimatan.jar:/usr/lib/jvm/java-7-oracle/jre/lib/jfxrt.jar  net.hetimatan.hetimatan.HtanClientMain
break;;

"tracker")
java -cp hetimatan.jar:/usr/lib/jvm/java-7-oracle/jre/lib/jfxrt.jar  net.hetimatan.hetimatan.HtanTrackerMain
break;;

"tool")
java -cp hetimatan.jar:/usr/lib/jvm/java-7-oracle/jre/lib/jfxrt.jar  net.hetimatan.hetimatan.HtanToolMain
break;;

esac
