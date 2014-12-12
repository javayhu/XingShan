from PIL import Image
from django.db import models

# Create your models here.

from django.db import models
# from django.utils.image import Image

class Project(models.Model):
    name = models.CharField(max_length=50, blank=False, null=False)
    content = models.CharField(max_length=10240, blank=False, null=False)
    startdate = models.DateTimeField(auto_now=True, blank=False, null=False)
    organiser = models.CharField(max_length=50, blank=False, null=False)
    target = models.FloatField(max_length=10, blank=True, null=True)
    email = models.EmailField(max_length=50, blank=True, null=True)
    # imgfile = models.ImageField(max_length=100, upload_to='projects')
    # because of SAE, change ImageField to CharField
    imgfile = models.CharField(max_length=100, blank=False, null=False)
    state = models.BooleanField(default=False, blank=False, null=False)
    pcount = models.IntegerField(max_length=10, default=0)
    mcount = models.FloatField(max_length=10, default=0)

    def save(self, *args, **kwargs):
        super(Project, self).save(*args, **kwargs)  # Call the "real" save() method.
        # failed to resize the images, now using img tag in html to resize the image show
        # im = Image.open(self.imgfile.path)
        # new_size = 500, 280
        # im.resize(new_size)
        # im.save(self.imgfile.path)

    def __str__(self):  # __unicode__ on Python 2
        return self.name

    def __unicode__(self):  # __unicode__ on Python 2
        return self.name

    class Meta:
        ordering = ['-startdate']


class Questionnair(models.Model):
    name = models.CharField(max_length=50, blank=False, null=False)
    startdate = models.DateTimeField(auto_now=True, blank=False, null=False)
    organiser = models.CharField(max_length=50, blank=False, null=False)
    money = models.IntegerField(max_length=10, blank=True, null=True) #here can only be int
    email = models.EmailField(max_length=50, blank=True, null=True)
    url = models.CharField(max_length=100, blank=False, null=False)
    state = models.BooleanField(default=False, blank=False, null=False)
    pcount = models.IntegerField(max_length=10, default=0)
    mcount = models.IntegerField(max_length=10, default=0)

    def __str__(self):  # __unicode__ on Python 2
        return self.name

    def __unicode__(self):  # __unicode__ on Python 2
        return self.name

    class Meta:
        ordering = ['-startdate']