from django.db import models
from django.db.models.fields.files import ImageFieldFile
from django.core.serializers.json import DjangoJSONEncoder
    
class Image(models.Model):
    desc = models.CharField(max_length=20)
    url = models.ImageField(upload_to='images')
    
    def __unicode__(self):
        return self.desc
    
    def natural_key(self):
        return {"desc" : self.desc, "url" : self.url.url}
    
class Category(models.Model):
    name = models.CharField(max_length=20)
    
    def __unicode__(self):
        return self.name
    
    def natural_key(self):
        return self.name
    
class Building(models.Model):
    name = models.CharField(max_length=200)
    desc = models.TextField(max_length=2000)
    lat = models.FloatField()
    lng = models.FloatField()
    address = models.CharField(max_length=200)
    open_time = models.CharField(max_length=100)
    images = models.ManyToManyField(Image, blank=True)
    categories = models.ManyToManyField(Category, blank=False)
    
    def __unicode__(self):
        return self.name
    
class LazyEncoder(DjangoJSONEncoder):
    def default(self, obj):
        if isinstance(obj, ImageFieldFile):
            return obj.path
        return super(LazyEncoder, self).default(obj)