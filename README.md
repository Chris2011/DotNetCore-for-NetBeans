<h1>.NET Core for NetBeans (Project: Goliath (WORK IN PROGRESS)</h1>
<h1>Knowing Problems</h1>
It is not possible to find a full featured grammar file for C#. So for this, I need to rethink of other implementations.
Maybe OmniSharp could be an alternative or, what makes more sense is to use LSP for C#.
So if anyone else has other ideas or can contribute, it will be great.

See this tickets for more information:

- https://github.com/dotnet/roslyn/issues/8379
- https://github.com/dotnet/csharplang/issues/1902


---------------------------------------


Brings .NET Core with C# support to netbeans (Create projects, Open Projects, Syntax highlighting, etc.)

<img src="screenshots/david-vs-goliath.jpg" alt="David vs Goliath" />

<img src="screenshots/wpfAppInNetBeans.png" alt="opened wpf project in netbeans" />


<h2>Features</h2>
<ol>
    <li>Open a Solution (.sln) created from Visual Studio</li>
    <li>Create a C# Project with Netbeans (Added new project type)</li>
    <li>Opens the project with all files</li>
    <li>New filetypes where added (.cs, .config)</li>
    <li>List of resources are available as nodes in the project view</li>
    <li>Basic Syntax Highlighting for .cs files</li>
    <li>Braces matching</li>
    <li>Reformat code</li>
</ol>

<p>If you find some bugs please report it.</p>
