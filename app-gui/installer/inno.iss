; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Story Inspector"
#define MyAppVersion "Alpha"
#define MyAppPublisher "Story Inspector"
#define MyAppURL "https://www.storyinspector.com/"
#define MyAppExeName "StoryInspector.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{009B36E9-CAA2-468B-BE4B-30E189361374}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\StoryInspector
DisableDirPage=no
DisableProgramGroupPage=yes
; Remove the following line to run in administrative install mode (install for all users.)
PrivilegesRequired=lowest
OutputDir=C:\Users\tdper\OneDrive\Desktop
OutputBaseFilename=StoryInspector-installer-win-amd64
SetupIconFile=C:\Dev\story-inspector\app-gui\src\main\resources\install.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Dev\story-inspector\app-gui\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Dev\story-inspector\app-gui\JRE\*"; DestDir: "{app}\JRE"; Flags: recursesubdirs createallsubdirs
Source: "C:\Dev\story-inspector\app-gui\A._Conan_Doyle-A_Study_in_Scarlett.storydom"; DestDir: "{app}"; Flags: onlyifdoesntexist
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
