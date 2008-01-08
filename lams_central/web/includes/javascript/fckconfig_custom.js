FCKConfig.ToolbarSets["Default"] = [
	['Source','-','FitWindow', 'Preview','PasteWord','Undo','Redo','Bold','Italic','Underline', '-','Subscript','Superscript','OrderedList','UnorderedList','-','Outdent','Indent','JustifyLeft','JustifyCenter','JustifyRight','JustifyFull','TextColor','BGColor'], 
	['Image','Link','Table','Rule','Smiley','SpecialChar','Templates','FontFormat','FontName','FontSize','About']
] ;

FCKConfig.ToolbarSets["Default-Learner"] = [
	['Preview','PasteWord'],
	['Undo','Redo'],
	['Bold','Italic','Underline', '-','Subscript','Superscript'],
	['OrderedList','UnorderedList','-','Outdent','Indent'],
	['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
	['About'],
	['TextColor','BGColor'],
	['Table','Rule','Smiley','SpecialChar'],
	['FontFormat','FontName','FontSize']
] ;

FCKConfig.EditorAreaCSS = FCKConfig.BasePath + '../../css/defaultHTML_learner.css' ;
FCKConfig.SkinPath = FCKConfig.BasePath + 'skins/office2003/' ;
FCKConfig.FirefoxSpellChecker = true;
FCKConfig.BrowserContextMenuOnCtrl = true;
FCKConfig.DefaultLinkTarget = "_blank";
FCKConfig.TemplatesXmlPath = FCKConfig.BasePath + '../../www/htmltemplates.xml'
