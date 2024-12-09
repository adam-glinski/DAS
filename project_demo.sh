#!/bin/bash
cd ./out/production/DAS

java DAS 9998 15 > output.log & # Master

java DAS 9998 10 # Slave 1 (send 10)
java DAS 9998 0 # Slave 2 (broadcast avg)
sleep 5
java DAS 9998 -1 # Slave 3 (broadcast exit)

wait
cat output.log
rm -f output.log
