<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Client extends Model
{
    protected $fillable = [
        'client_code',
        'name',
        'company',
        'mobile',
        'alternate_mobile',
        'email',
        'gst_number',
        'city',
        'state',
        'address',
        'plan',
        'expiry_date',
        'status',
        'notes',
    ];

    public function licenses(): HasMany
    {
        return $this->hasMany(License::class);
    }
    public function activations()
{
    return $this->hasMany(Activation::class);
}
}