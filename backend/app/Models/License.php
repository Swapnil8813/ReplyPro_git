<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class License extends Model
{
    protected $fillable = [

        'client_id',
        'license_key',
        'product_name',
        'plan',
        'device_limit',
        'activated_devices',
        'purchase_date',
        'expiry_date',
        'status',
        'remarks'

    ];

    public function client()
    {
        return $this->belongsTo(Client::class);
    }
    public function devices()
{
    return $this->hasMany(Device::class);
}
}