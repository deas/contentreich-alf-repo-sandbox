package alfresco.templates.webscripts.test

def test() {
	"42"
}

def answer = test()

model.groovyValue = answer 