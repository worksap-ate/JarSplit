JarSplit
========


依存ライブラリ
-------------

1. ASM 4.1 http://forge.ow2.org/project/download.php?group_id=23&file_id=18542
2. gnu trove 3.03 https://bitbucket.org/robeden/trove/downloads
3. scc.jar https://github.com/cloverrose/StronglyConnectedComponent

開発環境の構築
-------------
jarの作成、コンパイルは現在コマンドラインからではなくて、Eclipseでやってます

1. scc.jarを作成
このプログラムもgnu troveに依存しています

2. 依存ライブラリを/usr/local/libなどに置く

3. 分割ターゲットとして適当なJar(hoge.jar)を入手する

4. 実行

```
time java -Xss10m -classpath .:/usr/local/lib/trove-3.0.3.jar:/usr/local/lib/asm-4.1.jar:/usr/local/lib/scc.jar main/Main ~/Download/hoge.jar
```

カレントディレクトリにjarsample0.jar~jarsample4.jarが出来上がる。