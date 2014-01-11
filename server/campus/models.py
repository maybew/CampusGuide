from django.db import models

class Building(models.Model):
    name = models.CharField(max_length=200)
    desc = models.TextField(max_length=2000)
    lat = models.FloatField()
    lng = models.FloatField()
    img = models.ImageField(upload_to='images')
    
    def __unicode__(self):
        return self.name