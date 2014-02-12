require 'nokogiri'
require 'pp'

#slurp version
#
pom = Nokogiri::XML(open('pom.xml'))
pomversionnode = pom.xpath("//pom:project/pom:version", {pom: "http://maven.apache.org/POM/4.0.0"})[0]
pomversion = pomversionnode.text
puts "pom version is " + pomversion
