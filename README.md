JarSplit
========

Build
------
```
mvn package
```

Usage
------
```
java -jar target/jarsplit-1.0-SNAPSHOT-jar-with-dependencies.jar ~/Download/hoge.jar 5
```


 - 第1引数は分割するjarファイル
 - 第2引数は分割数


カレントディレクトリにsplitted0.jar~splitted4.jarが出来上がる。
ただし入力するjarの依存関係が密な場合は出力されるjarの数は5未満になる。