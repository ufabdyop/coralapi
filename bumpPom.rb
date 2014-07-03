#!/usr/bin/env ruby
require 'nokogiri'
require 'pp'

#slurp version
#
pom = Nokogiri::XML(open('pom.xml'))
pomversionnode = pom.xpath("//pom:project/pom:version", {pom: "http://maven.apache.org/POM/4.0.0"})[0]
pomversion = pomversionnode.text
$stderr.puts "original version number: " + pomversion
versionparts = pomversion.split(/\.|\-/)
if versionparts[3] == 'SNAPSHOT'
  versionparts[3] = ''
else
  versionparts[2] = versionparts[2].to_i + 1
  versionparts[3] = '-SNAPSHOT'
end

#new version
pomversion = versionparts[0..2].join('.') + versionparts[3]
$stderr.puts "new version number: " + pomversion
pomversionnode.content = pomversion

$stderr.puts "writing new version #{pomversion} to pom.xml"
File.open('pom.xml','w') {|f| pom.write_xml_to f}

puts pomversion
