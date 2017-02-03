### Scott Logic Beamer template

This is a default template for use in Beamer LaTeX presentations.

#### To install

<aside class="notice">
Note: I've only tested these instructions with MikTex, not with TexLive.
</aside>

* Create a directory to store local Beamer themes in. I created mine at `C:\Users\USERNAME\localmiktex`.
* Within that directory, create the directory structure `tex\latex\beamer\themes\theme`.
* Copy the template file into the `theme\` directory:
```
cp beamerthemeScottLogic.sty C:\Users\USERNAME\localmiktex\tex\latex\beamer\themes\theme\
```
* Assuming the MikTex `bin` dir is on your path, run the following commands to register your local styles directory

<aside class="notice">
Note: If you're running these commands as admin, you must add `--admin` to each command.
</aside>

```
initexmf --register-root=C:\Users\USERNAME\localmiktex
initexmf --update-fndb
```

#### To use

Call the theme after declaring your `documentclass`. 
```
\documentclass{beamer}
\usetheme{ScottLogic}
\title{My Title}
\date{\today}

\begin{document}

\begin{frame}
All the best words
\end{frame}

\end{document}
```

This will use the default 4:3 aspect ratio. If you want the 16:9 aspect ratio, pass in the relevant option to `documentclass`
```
\documentclass[aspectratio=169]{beamer}
```
