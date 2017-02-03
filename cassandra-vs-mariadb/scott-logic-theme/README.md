### Scott Logic Beamer template

This is a default template for use in Beamer LaTeX presentations.

#### To install

<aside class="notice">
Note: I've only tested these instructions with MikTex, not with TexLive.
</aside>

* Create a directory to store local Beamer themes in. I created mine at `C:\Users\USERNAME\localmiktex`.
* Within that directory, create the directory structure `tex\latex\beamer\themes\theme`.
* Copy the template files into the `theme\` directory:
```
cp beamerthemeScottLogic.sty C:\Users\USERNAME\localmiktex\tex\latex\beamer\themes\theme\
cp full-scott-logic.png C:\Users\USERNAME\localmiktex\tex\latex\beamer\themes\theme\  
cp scott-logic-footer.png C:\Users\USERNAME\localmiktex\tex\latex\beamer\themes\theme\
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
  \titlepage
\end{frame}

\section{First section}
\subsection{First subsection}

\begin{frame}
  All the best words
\end{frame}

\end{document}

```

This will use the default 4:3 aspect ratio. If you want the 16:9 aspect ratio, pass in the relevant option to `documentclass`
```
\documentclass[aspectratio=169]{beamer}
```

#### Overview of theme

In brief, the theme
* Gives a navigation header based on section and subsection
* Generates a title page for each section
* Adds a final branded page to each presentation
* Introduces a `code` environment. To grey out some code, use the delimeters `<@` and `@>`. For example
```
  \begin{block}{cluster.tf}
    \begin{code}{ruby}
module "test-client" {
  <@source               = "../resources/cluster/test-client"
  ami                  = "${var.test-client_ami}"
  key_name             = "${var.key_name}"
  mariadb_password     = "${var.mariadb_password}"
  security_group_name  = "${var.security_group_name}"
  private_key          = "${var.private_key}"
  cluster_name         = "${var.cluster_name}"@>
  cassandra_primary_ip = "${module.cassandra.primary_ip}"
  mariadb_ips          = "${join(",", module.mariadb.private_ips)}"
}
    \end{code}
  \end{block}
```
