#!/bin/sh

echo $0
pat=$1

case "$pat" in
"seeder") 
echo "start seeder"
javac -cp ./Hetimatan/src ./Hetimatan/src/net/hetimatan/MainStartTracker.java
java -cp ./Hetimatan/src net.hetimatan.MainStartTracker ./Hetimatan/testdata/1m_a.txt.torrent
break ;;

"meta")
echo "start create meta"
add=$2
javac -cp ./Hetimatan/src ./Hetimatan/src/net/hetimatan/MainCreateTorrentFile.java
java -cp ./Hetimatan/src net.hetimatans.MainCreateTorrentFile ${add} ./Hetimatan/testdata/1mb/1m_a.txt 
break;;
"clean")
a=`find . -name "*.class"`
for b in ${a}
do
rm $b
done

a=`find . -name "virtu*"`
for b in ${a}
do
rm $b
done

break;;
esac

# javac -cp ./Hetimatan/src ./Hetimatan/src/net/hetimatan/MainShowNICAddress.java


#java -cp ./Hetimatan/src net.hetimatan.MainShowNICAddress
#java -cp ./Hetimatan/src net.hetimatan.MainStartTracker ./Hetimatan/testdata/1kb.torrent


