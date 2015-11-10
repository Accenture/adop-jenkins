require "serverspec"
require "docker"

describe "Dockerfile" do
  before(:all) do
    image = Docker::Image.build_from_dir('/docker-file/.')

    set :os, family: :debian
    set :backend, :docker
    set :docker_image, image.id
  end

  describe file('/usr/share/jenkins/ref/') do
   it { should be_directory }
  end

  describe file('/usr/share/jenkins/ref/init.groovy.d/') do
   it { should be_directory }
  end

  describe file('/usr/share/jenkins/ref/adop_scripts/') do
   it { should be_directory }
  end

  describe file('/usr/share/jenkins/ref/adop_scripts/') do
   it { should be_owned_by 'root' }
  end


end
