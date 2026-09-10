<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Laravel\Sanctum\HasApiTokens;

class Customer extends Model
{
    use HasApiTokens;

    protected $fillable = [
        'name',
        'mobile',
        'email',
        'token_number',
        'license_id',
        'device_id',
        'status',
        'last_login'
    ];

    protected $casts = [
        'last_login' => 'datetime',
    ];

    public function license()
    {
        return $this->belongsTo(License::class);
    }

    public function devices()
    {
        return $this->hasMany(Device::class);
    }
}